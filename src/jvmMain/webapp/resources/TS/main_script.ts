import {Plot} from "./plot.js";
import {PointCheckTableManager} from "./table/point_check_table_manager.js";

let main_plot = new Plot("box1");
export let point_check_table_manager = new PointCheckTableManager("resultTable_data");

function getQuack() {
    return new Audio("resources/mp3/duck.mp3");
}

function drawAllPointsFromTable() {
    const rows = point_check_table_manager.getRows(0);

    for (const [x, y, r, isInArea] of rows) {
        if (isNaN(x)) {
            point_check_table_manager.clearTable(0);
            return;
        }

        main_plot.drawPoint(x, y, r, isInArea ? "green" : "red");
    }

    main_plot.redrawPoints(getR());
}

// @ts-ignore
onDataSubmit = () => {
    point_check_table_manager.updateFromHtml();
    const rows = point_check_table_manager.getRows(point_check_table_manager.getRowsCount() - 1);

    for (const [x, y, r, isInArea] of rows) {
        main_plot.drawPoint(x, y, r, isInArea ? "green" : "red");
    }

    getQuack().play().then(() => {
    });
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

    if (Math.abs(x) > 3 || y > 3 || y < -5) {
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
onRChange = () => {
    main_plot.redrawPoints(getR());
}

// @ts-ignore
onRemoveAllPoints = () => {
    point_check_table_manager.updateFromHtml();
    point_check_table_manager.clearTable(0);
    main_plot.removeAllPoints();
}

// @ts-ignore
$(document).ready(() => {
    drawAllPointsFromTable();
});
