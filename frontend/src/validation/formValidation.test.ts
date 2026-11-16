import {describe, expect, it} from 'vitest'
import {validateForm} from './formValidation.ts'
import type {FormValues} from '../types/listing.ts'

const empty: FormValues = {
    minPrice: '',
    maxPrice: '',
    minBedrooms: '',
    city: '',
    keyword: '',
    targetBudget: '',
    status: 'active',
}

const withValues = (changes: Partial<FormValues>): FormValues => ({...empty, ...changes})

describe('validateForm', () => {
    it('accepts an empty form', () => {
        expect(validateForm(empty)).toEqual([])
    })

    it('rejects text in a numeric field', () => {
        expect(validateForm(withValues({minPrice: 'abc'}))).toEqual([
            {field: 'minPrice', message: 'must be a number'},
        ])
    })

    it('rejects negative bedrooms', () => {
        expect(validateForm(withValues({minBedrooms: '-1'}))).toEqual([
            {field: 'minBedrooms', message: 'must be 0 or more'},
        ])
    })
})