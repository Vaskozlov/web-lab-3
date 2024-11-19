import {Plot} from "./plot.js";
import {PointCheckTableManager} from "./table/point_check_table_manager.js";

export let point_check_table_manager = new PointCheckTableManager("resultTable_data");

let main_plot = new Plot("box1");

const rows = point_check_table_manager.getRows(0);

for (const [x, y, r, is_in_area] of rows) {
    main_plot.drawPoint(x, y, r, is_in_area ? "green" : "red");
}

// @ts-ignore
window.customFunction = (x: number, y: number, r: number) => {
    point_check_table_manager.updateFromHtml();

    for (const [x, y, r, is_in_area] of point_check_table_manager.getRows(point_check_table_manager.getRowsCount() - 1)) {
        main_plot.drawPoint(x, y, r, is_in_area ? "green" : "red");
    }
}

main_plot.setOnClickFunction(onPlotClick);

function getR() {
    // @ts-ignore
    const rInputWidget = PrimeFaces.widgets.rInput
    return parseFloat(rInputWidget.getValue());
}

function onPlotClick(x: number, y: number) {
    let submit_button = document.getElementById("mainForm:submitButton") as HTMLButtonElement;
    const r = getR();
    x *= r;
    y *= r;

    if (Math.abs(x) > 3) {
        return;
    }

    if (y > 3 || y < -5) {
        return;
    }

    // @ts-ignore
    const xInputWidget = PrimeFaces.widgets.xInput

    // @ts-ignore
    xInputWidget.setValue(x.toString())

    // @ts-ignore
    PrimeFaces.widgets.yInput.setValue(y.toString())

    submit_button.click();

    // @ts-ignore
    PrimeFaces.ajax.AjaxRequest({
        source: xInputWidget.id,
        process: xInputWidget.id,
        update: 'mainForm:xLabel mainForm:xSlider'
    });
}

// @ts-ignore
window.onRChange = () => {
    main_plot.redrawPoints(getR());
}

// @ts-ignore
window.removeAllPoints = () => {
    point_check_table_manager.updateFromHtml();
    point_check_table_manager.clearTable(0);
    main_plot.removeAllPoints();
}
