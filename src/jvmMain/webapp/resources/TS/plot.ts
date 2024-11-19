// @ts-ignore
import JXG from 'https://cdn.jsdelivr.net/npm/jsxgraph/distrib/jsxgraphcore.mjs';

export class Plot {
    private static x_axis_config = {
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

    private static y_axis_config = {
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
    private readonly created_points: any[] = [];

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
                x: Plot.x_axis_config,
                y: Plot.y_axis_config
            },
        });

        this.board.highlightInfobox = function (x: any, y: any, el: any) {
            this.infobox.setText(`(${x}R, ${y}R, R=${el.visProp.radius})`);
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

    public drawPoint(x: number, y: number, r: number, color: string): void {
        console.log(x, y, r, color);
        this.created_points.push(
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
            }));
    }

    public redrawPoints(new_r: number) {

        const points_copy = this.created_points.slice();
        this.created_points.length = 0;

        this.board.suspendUpdate();

        for (const point of points_copy) {
            this.board.removeObject(point);
            const [_, x, y] = point.coords.usrCoords;

            this.drawPoint(
                x * point.visProp.radius,
                y * point.visProp.radius,
                new_r,
                point.visProp.fillcolor
            );
        }

        this.board.unsuspendUpdate();
    }

    public removeAllPoints(): void {
        this.board.suspendUpdate();

        this.created_points.forEach(point => this.board.removeObject(point));
        this.created_points.length = 0;

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
