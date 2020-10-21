import { Component, Input, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts/highstock';

@Component({
    selector: 'summary-chart',
    templateUrl: './summary-chart.component.html',
    styles: [
        `
          highcharts-chart {
            display: block;
          }
        `
    ]
})

export class SummaryChartComponent implements OnInit {
    @Input() ticker: string;
    @Input() data: any;
    @Input() isPositiveChange: boolean;
    Highcharts: typeof Highcharts = Highcharts;
    chartOptions: Highcharts.Options;
    chartConstructor: string = 'stockChart';
    
    ngOnInit() {
        const date = this.data.date_array;
        const price = this.data.price_array;
        const dataArr = [];
        for (let i = 0; i < date.length; i++) {
            dataArr.push([
                date[i],
                price[i]
            ])
        }
        this.chartOptions = {
            title: {
                text: this.ticker,
                style: {
                    color: "gray"
                }
            },
            series: [{
              data: dataArr,
              name: this.ticker,
              type: 'line',
              tooltip: {
                  valueDecimals: 2
              }
            }],
            rangeSelector: {
                enabled: false
            },
            plotOptions: {
                series: {
                    color: this.isPositiveChange ? "green" : "red"
                }
            },
            time: {
                timezoneOffset: 7 * 60
            }
        }
    }
}