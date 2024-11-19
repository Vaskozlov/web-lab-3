export class RowBuilder {
    constructor(table) {
        this.row = table.insertRow(-1);
    }
    addCell(cell) {
        return this.addStringCell(cell.innerText);
    }
    addStringCell(value) {
        const cell = this.row.insertCell(-1);
        cell.innerText = value.trim();
        return this;
    }
    addCells(cells) {
        for (const cell of cells) {
            this.addCell(cell);
        }
        return this;
    }
}
