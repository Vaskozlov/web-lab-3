import { RowBuilder } from "./row_builder.js";
export class HtmlTableManager {
    constructor(tableName) {
        this.tableName = tableName;
        this.updateFromHtml();
    }
    updateFromHtml() {
        this.table = document.getElementById(this.tableName);
    }
    getRowsCount() {
        return this.table.rows.length;
    }
    addRow() {
        return new RowBuilder(this.table);
    }
    removeRow(row_index) {
        this.table.deleteRow(row_index);
    }
    clearTable(start) {
        while (this.table.rows.length > start) {
            this.table.deleteRow(start);
        }
    }
}
