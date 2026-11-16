import { useState } from 'react'
import { ApiValidationError, searchListings } from './api/listingsApi'
import Pagination from './components/Pagination'
import ResultsTable from './components/ResultsTable'
import SearchForm from "./components/SearchForm";
import type {FieldError, FormValues, SearchResponse} from './types/listing'
import { validateForm } from './validation/formValidation.ts'

const emptyForm: FormValues = {
    minPrice: '',
    maxPrice: '',
    minBedrooms: '',
    city: '',
    keyword: '',
    targetBudget: '',
    status: 'active'
}

function App() {
    const [form, setForm] = useState<FormValues>(emptyForm)
    const [submitted, setSubmitted] = useState<FormValues>(emptyForm)
    const [pageSize, setPageSize] = useState(10)
    const [result, setResult] = useState<SearchResponse | null>(null)
    const [loading, setLoading] = useState(false)
    const [fieldErrors, setFieldErrors] = useState<FieldError[]>([])
    const [error, setError] = useState<string | null>(null)
    const clientErrors = validateForm(form)
    const errors = [...clientErrors, ...fieldErrors]

    function resetSearch() {
        setForm(emptyForm)
        setSubmitted(emptyForm)
        setResult(null)
        setFieldErrors([])
        setError(null)
    }

    async function runSearch(filters: FormValues, page: number, size:number) {
        setLoading(true);
        setError(null);
        setFieldErrors([])
        try {
            setResult(await searchListings({ ...filters, page, pageSize: size }))
            setSubmitted(filters)
        } catch (e) {
            setResult(null)
            if (e instanceof ApiValidationError) {
                setFieldErrors(e.errors)
            } else {
                setError('Something went wrong. Please try again.')
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="page">
            <h1>Listing Search</h1>

            <SearchForm
                values={form}
                errors={errors}
                loading={loading}
                onChange={(field, value) => {
                    setForm((prev) => ({ ...prev, [field]: value }))
                    setFieldErrors([])
                }}
                onSubmit={() => runSearch(form, 1, pageSize)}
                onReset={resetSearch}
            />

            {error && <p className="error">{error}</p>}
            {loading && !result && <p className="summary">Loading…</p>}
            {result && (
                <>
                    <div className="summary-row">
                        <div>
                            <p className="summary">{result.totalItems} listings found</p>
                            {result.message && (
                                <p className="summary">
                                    <em>{result.message}</em>
                                </p>
                            )}
                        </div>
                        {result.totalPages > 0 && (
                            <Pagination
                                page={result.page}
                                totalPages={result.totalPages}
                                pageSize={pageSize}
                                loading={loading}
                                onPageChange={(page) => runSearch(submitted, page, pageSize)}
                                onPageSizeChange={(size) => {
                                    setPageSize(size)
                                    runSearch(submitted, 1, size)
                                }}
                            />
                        )}
                    </div>
                    {result.items.length > 0 && <ResultsTable items={result.items} />}
                </>
            )}
        </main>
    )
}
export default App