import type {ListingItem} from '../types/listing'

type Props = {
    items: ListingItem[]
}

const money = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0
})

export default function ResultsTable({items}: Props) {
    return (
        <table className="results">
            <thead>
            <tr>
                <th>Address</th>
                <th>City</th>
                <th className="num">Price</th>
                <th className="num">Beds</th>
                <th className="num">Score</th>
            </tr>
            </thead>
            <tbody>
            {items.map((item) => (
                <tr key={`${item.source}-${item.id}`}>
                    <td>{item.address}</td>
                    <td>{item.city}</td>
                    <td className="num">{money.format(item.price)}</td>
                    <td className="num">{item.bedrooms}</td>
                    <td className="num">{item.score.toFixed(1)}</td>
                </tr>
            ))}
            </tbody>
        </table>
    )
}