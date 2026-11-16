import type {FieldError, SearchParams, SearchResponse} from '../types/listing'

export class ApiValidationError extends Error {
    errors: FieldError[]

    constructor(errors: FieldError[]) {
        super('Invalid search parameters')
        this.errors = errors
    }
}

export async function searchListings(params: SearchParams): Promise<SearchResponse> {
    const query = new URLSearchParams()
    for (const [key, value] of Object.entries(params)) {
        const text = String(value).trim()
        if (text !== '') {
            query.set(key, text)
        }
    }

    const response = await fetch(`/api/listings/search?${query}`)

    // the API returns a ProblemDetail with an errors[] list; surface it per field
    if (response.status === 400) {
        const problem = await response.json()
        throw new ApiValidationError(problem.errors ?? [])
    }

    if (!response.ok) {
        throw new Error(`Server error (${response.status})`)
    }
    return response.json()
}
