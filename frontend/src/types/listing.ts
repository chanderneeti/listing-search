export type ListingItem = {
    id: string
    source: string
    address: string
    city: string
    state: string
    zip: string
    price: number
    bedrooms: number
    bathrooms: number
    sqft: number
    listedDate: string
    status: string
    score: number
}

export type SearchResponse = {
    items: ListingItem[]
    page: number
    pageSize: number
    totalItems: number
    totalPages: number
    message: string | null
}

export type FieldError = {
    field: string
    message: string
}

export type SearchParams = {
    minPrice: string
    maxPrice: string
    minBedrooms: string
    city: string
    keyword: string
    targetBudget: string
    status: string
    page: number
    pageSize: number
}

export type FormValues = Omit<SearchParams, 'page' | 'pageSize'>