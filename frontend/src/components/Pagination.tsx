type Props = {
    page: number
    totalPages: number
    pageSize: number
    loading: boolean
    onPageChange: (page: number) => void
    onPageSizeChange: (size: number) => void
}

const pageSizes = [5, 10, 20, 50]

export default function Pagination({
                                       page,
                                       totalPages,
                                       pageSize,
                                       loading,
                                       onPageChange,
                                       onPageSizeChange
                                   }: Props) {
    return (
        <div className="pagination">
            <button onClick={() => onPageChange(page - 1)} disabled={loading || page <= 1}>
                Previous
            </button>
            <span>
        Page {page} of {totalPages}
      </span>
            <button onClick={() => onPageChange(page + 1)} disabled={loading || page >= totalPages}>
                Next
            </button>
            <label>
                Per page{' '}
                <select value={pageSize} onChange={(e) => onPageSizeChange(Number(e.target.value))}>
                    {pageSizes.map((size) => (
                        <option key={size} value={size}>
                            {size}
                        </option>
                    ))}
                </select>
            </label>
        </div>
    )
}