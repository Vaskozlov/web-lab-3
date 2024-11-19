export class RowBuilder {
    private readonly row: HTMLTableRowElement;

    public constructor(table: HTMLTableElement) {
        this.row = table.insertRow(-1);
    }

    public addCell(cell: HTMLTableCellElement): RowBuilder {
        return this.addStringCell(cell.innerText);
    }

    public addStringCell(value: string): RowBuilder {
        const cell = this.row.insertCell(-1);
        cell.innerText = value.trim();
        return this;
    }

    public addCells(cells: HTMLCollectionOf<HTMLTableCellElement>): RowBuilder {
        for (const cell of cells) {
            this.addCell(cell);
        }

        return this;
    }
}
