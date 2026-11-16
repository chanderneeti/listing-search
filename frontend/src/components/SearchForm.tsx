import type {FieldError, FormValues} from "../types/listing.ts"

type props = {
    values: FormValues
    errors: FieldError[]
    loading: boolean
    onChange: (field: keyof FormValues, value: string) => void
    onSubmit: () => void
    onReset: () => void
}

const knownFields = ['minPrice', 'maxPrice', 'minBedrooms', 'city', 'keyword', 'targetBudget', 'status']

export default function SearchForm({values, errors, loading, onChange, onSubmit, onReset}: props) {
    const errorFor = (field: string) => errors.find((e) => e.field === field)?.message
    const otherErrors = errors.filter((e) => !knownFields.includes(e.field))

    const textField = (field: keyof FormValues, label: string) => (
        <div className="field">
            <label>
                <span>{label}</span>
                <input
                    type="text"
                    value={values[field]}
                    onChange={(e) => onChange(field, e.target.value)}
                />
            </label>
            {<small className="error">{errorFor(field)}</small>}
        </div>
    )

    return (
        <form
            className="search-form"
            onSubmit={(e) => {
                e.preventDefault()
                onSubmit()
            }}
        >
            {otherErrors.length > 0 && (
                <ul className="error-list">
                    {otherErrors.map((e) => (
                        <li key={e.field}>{e.message}</li>
                    ))}
                </ul>
            )}

            {textField('minPrice', 'Min price')}
            {textField('maxPrice', 'Max price')}
            {textField('minBedrooms', 'Min bedrooms')}
            {textField('city', 'City')}
            {textField('keyword', 'Keyword')}
            {textField('targetBudget', 'Target budget')}

            <div className="field">
                <label>
                    <span>Status</span>
                    <select value={values.status} onChange={(e) => onChange('status', e.target.value)}>
                        <option value="active">active</option>
                        <option value="pending">pending</option>
                        <option value="sold">sold</option>
                    </select>
                </label>
                {<small className="error">{errorFor('status')}</small>}
            </div>
            <div className="field actions">
                <div className="spacer" aria-hidden="true">&nbsp;</div>
                <div className="button-row">
                    <button type="submit" className="primary" disabled={loading || errors.length > 0}>
                        {loading ? 'Searching…' : 'Search'}
                    </button>
                    <button type="button" onClick={onReset} disabled={loading}>
                        Reset
                    </button>
                </div>
                <small className="error"></small>
            </div>
        </form>
    )
}