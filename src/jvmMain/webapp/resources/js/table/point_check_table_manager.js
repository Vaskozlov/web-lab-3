import { HtmlTableManager } from "./html_table_manager.js";
import { IllegalStateError } from "../lib/illegal_state_error.js";
export class PointCheckTableManager extends HtmlTableManager {
    constructor(tableName) {
        super(tableName);
    }
    *getRows(from) {
        if (from < 0 || !Number.isInteger(from)) {
            throw new IllegalStateError("Start index must be non-negative integer");
        }
        for (let i = from; i < this.table.rows.length; i++) {
            const row = this.table.rows[i];
            const trimmed_elements = [...row.cells]
                .map(cell => cell.innerText.trim());
            const values = trimmed_elements
                .slice(0, 3)
                .map(parseFloat);
            const [x, y, r] = values;
            const is_in_area = trimmed_elements[3] === "Да";
            yield [x, y, r, is_in_area];
        }
    }
}
