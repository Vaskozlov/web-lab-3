import {HtmlTableManager} from "./html_table_manager.js";
import {IllegalStateError} from "../lib/illegal_state_error.js";

export class PointCheckTableManager extends HtmlTableManager {
    public constructor(tableName: string) {
        super(tableName);
    }

    public* getRows(from: number): Generator<[number, number, number, boolean]> {
        if (from < 0 || !Number.isInteger(from)) {
            throw new IllegalStateError("Start index must be non-negative integer");
        }

        for (let i = from; i < this.table.rows.length; i++) {
            const row = this.table.rows[i];

            const trimmedElements = [...row.cells]
                .map(cell => cell.innerText.trim())

            const values = trimmedElements
                .slice(0, 3)
                .map(parseFloat);

            const [x, y, r] = values;
            const isInArea = trimmedElements[3] === "Да";

            yield [x, y, r, isInArea];
        }
    }
}
