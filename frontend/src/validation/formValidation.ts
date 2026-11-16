import type {FieldError, FormValues} from '../types/listing.ts'

export function validateForm(values: FormValues): FieldError[] {
    const errors: FieldError[] = []

    function check(field: keyof FormValues, rule: (n: number) => string | null) {
        const text = values[field].trim()
        if (text === '') {
            return
        }
        const n = Number(text)
        if (Number.isNaN(n)) {
            errors.push({field, message: 'must be a number'})
            return
        }
        const problem = rule(n)
        if (problem) {
            errors.push({field, message: problem})
        }
    }

    check('minPrice', (n) => (n < 0 ? 'must be 0 or more' : null))
    check('maxPrice', (n) => (n < 0 ? 'must be 0 or more' : null))
    check('minBedrooms', (n) => (n < 0 ? 'must be 0 or more' : null))
    check('targetBudget', (n) => (n <= 0 ? 'must be greater than 0' : null))

    if (
        values.minPrice.trim() !== '' &&
        values.maxPrice.trim() !== '' &&
        Number(values.minPrice) > Number(values.maxPrice)
    ) {
        errors.push({field: 'minPrice', message: 'must not exceed max price'})
    }

    return errors
}