// @ts-ignore
import JXG from 'https://cdn.jsdelivr.net/npm/jsxgraph/distrib/jsxgraphcore.mjs';
export class Plot {
    constructor(elementId) {
        this.created_points = [];
        this.board = JXG.JSXGraph.initBoard(elementId, {
            boundingbox: [-8, 8, 8, -8],
            showCopyright: false,
            showNavigation: false,
            zoomX: 6,
            zoomY: 6,
            zoom: {
                wheel: false
            },
            axis: true,
            defaultAxes: {
                x: Plot.x_axis_config,
                y: Plot.y_axis_config
            },
        });
        this.board.highlightInfobox = function (x, y, el) {
            this.infobox.setText(`(${x}R, ${y}R, R=${el.visProp.radius})`);
        };
        this.drawAreas();
    }
    setOnClickFunction(onClickFunction) {
        this.board.on('down', (board_event) => {
            const [x, y] = this.getOnClickCoordinates(board_event);
            onClickFunction(x, y);
        });
    }
    getOnClickCoordinates(event) {
        return this.board.getUsrCoordsOfMouse(event);
    }
    drawPoint(x, y, r, color) {
        console.log(x, y, r, color);
        this.created_points.push(this.board.create('point', [x / r, y / r], {
            size: 3.5,
            strokeColor: color,
            fillColor: color,
            name: '',
            withLabel: false,
            highlight: true,
            showInfobox: true,
            infoboxDigits: 2,
            radius: r,
        }));
    }
    redrawPoints(new_r) {
        const points_copy = this.created_points.slice();
        this.created_points.length = 0;
        this.board.suspendUpdate();
        for (const point of points_copy) {
            this.board.removeObject(point);
            const [_, x, y] = point.coords.usrCoords;
            this.drawPoint(x * point.visProp.radius, y * point.visProp.radius, new_r, point.visProp.fillcolor);
        }
        this.board.unsuspendUpdate();
    }
    removeAllPoints() {
        this.board.suspendUpdate();
        this.created_points.forEach(point => this.board.removeObject(point));
        this.created_points.length = 0;
        this.board.unsuspendUpdate();
    }
    drawAreas() {
        this.drawAreaInFirstQuarter();
        this.drawAreaInSecondQuarter();
        this.drawAreaInThirdQuarter();
        this.drawAreaInFourthQuarter();
    }
    drawAreaInFirstQuarter() {
        this.board.create("polygon", [[0, 0], [1, 0], [0, 0.5]], {
            fillcolor: "lightblue",
            fillOpacity: 0.8,
            withLines: false,
            vertices: {
                visible: false
            },
        });
    }
    drawAreaInSecondQuarter() {
    }
    drawAreaInThirdQuarter() {
        this.board.create("sector", [[0, 0], [-0.5, 0], [0, -0.5]], {
            fillcolor: "lightblue",
            fillOpacity: 0.8,
            vertices: {
                visible: false
            },
            radiuspoint: {
                visible: false
            },
            anglePoint: {
                visible: false
            },
            strokeWidth: 0,
        });
    }
    drawAreaInFourthQuarter() {
        this.board.create("polygon", [[0, 0], [1, 0], [1, -0.5], [0, -0.5]], {
            fillcolor: "lightblue",
            fillOpacity: 0.8,
            withLines: false,
            vertices: {
                visible: false
            },
            radius: 0,
        });
    }
}
Plot.x_axis_config = {
    name: 'x',
    withLabel: true,
    label: {
        position: 'rt',
        offset: [-15, 10]
    },
    ticks: {
        insertTicks: false,
        scaleSymbol: 'R',
        minorHeight: 0,
        ticksDistance: 0.5
    }
};
Plot.y_axis_config = {
    name: 'y',
    withLabel: true,
    label: {
        position: 'rt',
        offset: [10, 0]
    },
    ticks: {
        scaleSymbol: 'R',
        minorHeight: 0,
        insertTicks: false,
        ticksDistance: 0.5
    }
};
