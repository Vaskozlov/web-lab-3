// @ts-ignore
import JXG from 'https://cdn.jsdelivr.net/npm/jsxgraph/distrib/jsxgraphcore.mjs';

export class Plot {
    private static xAxisConfig = {
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

    private static yAxisConfig = {
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

    private readonly board: JXG.JSXGraph;
    private readonly createdPoints: any[] = [];

    public constructor(elementId: String) {
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
                x: Plot.xAxisConfig,
                y: Plot.yAxisConfig
            },
        });

        this.board.highlightInfobox = function (x: any, y: any, el: any) {
            this.infobox.setText(`(${x}R, ${y}R, R=${el.visProp.displayedRadius})`);
        };

        this.drawAreas();
    }

    public setOnClickFunction(onClickFunction: (x: number, y: number) => void): void {
        this.board.on('down', (board_event: any) => {
            const [x, y] = this.getOnClickCoordinates(board_event);
            onClickFunction(x, y);
        });
    }

    public getOnClickCoordinates(event: any): [number, number] {
        return this.board.getUsrCoordsOfMouse(event);
    }

    public drawPoint(x: number, y: number, r: number, color: string, realR: number = null): void {
        realR = realR === null ? r : realR;

        this.createdPoints.push(
            this.board.create('point', [x / r, y / r], {
                size: 3.5,
                strokeColor: color,
                fillColor: color,
                name: '',
                withLabel: false,
                highlight: true,
                showInfobox: true,
                infoboxDigits: 2,
                radius: r,
                displayedRadius: realR
            }));
    }

    public redrawPoints(newR: number) {

        const pointsCopy = this.createdPoints.slice();
        this.createdPoints.length = 0;

        this.board.suspendUpdate();

        for (const point of pointsCopy) {
            this.board.removeObject(point);
            const [_, x, y] = point.coords.usrCoords;

            this.drawPoint(
                x * point.visProp.radius,
                y * point.visProp.radius,
                newR,
                point.visProp.fillcolor,
                point.visProp.displayedRadius
            );
        }

        this.board.unsuspendUpdate();
    }

    public removeAllPoints(): void {
        this.board.suspendUpdate();

        this.createdPoints.forEach(point => this.board.removeObject(point));
        this.createdPoints.length = 0;

        this.board.unsuspendUpdate();
    }

    public drawAreas(): void {
        this.drawAreaInFirstQuarter();
        this.drawAreaInSecondQuarter();
        this.drawAreaInThirdQuarter();
        this.drawAreaInFourthQuarter();
    }

    private drawAreaInFirstQuarter() {
        this.board.create(
            "polygon",
            [[0, 0], [1, 0], [0, 0.5]], {
                fillcolor: "lightblue",
                fillOpacity: 0.8,
                withLines: false,
                vertices: {
                    visible: false
                },
            })
    }

    public drawAreaInSecondQuarter(): void {

    }

    private drawAreaInThirdQuarter() {
        this.board.create("sector",
            [[0, 0], [-0.5, 0], [0, -0.5]],
            {
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
            })
    }

    private drawAreaInFourthQuarter(): void {
        this.board.create(
            "polygon",
            [[0, 0], [1, 0], [1, -0.5], [0, -0.5]], {
                fillcolor: "lightblue",
                fillOpacity: 0.8,
                withLines: false,
                vertices: {
                    visible: false
                },
                radius: 0,
            })
    }
}
