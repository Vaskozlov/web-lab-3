import {RowBuilder} from "./row_builder.js";

export class HtmlTableManager {
    protected table: HTMLTableElement;
    protected readonly tableName: string

    public constructor(tableName: string) {
        this.tableName = tableName;
        this.updateFromHtml();
    }

    public updateFromHtml() {
        this.table = document.getElementById(this.tableName) as HTMLTableElement;
    }

    public getRowsCount(): number {
        return this.table.rows.length;
    }

    public addRow(): RowBuilder {
        return new RowBuilder(this.table);
    }

    public removeRow(row_index: number): void {
        this.table.deleteRow(row_index);
    }

    public clearTable(start: number): void {
        while (this.table.rows.length > start) {
            this.table.deleteRow(start);
        }
    }
}
