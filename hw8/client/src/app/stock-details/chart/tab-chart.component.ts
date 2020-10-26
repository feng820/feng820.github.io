import { Component, OnInit, Input} from '@angular/core';
import * as Highcharts from 'highcharts/highstock';
import IndicatorsCore from "highcharts/indicators/indicators";
import vbp from 'highcharts/indicators/volume-by-price';
IndicatorsCore(Highcharts);
vbp(Highcharts);

@Component({
  selector: 'tab-chart',
  templateUrl: './tab-chart.component.html',
  styles: [
    `
      highcharts-chart {
        display: block;
      }
    `
  ]
})
export class TabChartComponent implements OnInit {
    @Input() ticker: string;
    @Input() data: any;
    Highcharts: typeof Highcharts = Highcharts;
    chartOptions: Highcharts.Options;
    chartConstructor: string = 'stockChart';
    
    ngOnInit() {
        const date = this.data.date_array;
        const open = this.data.open_price_array;
        const high = this.data.high_price_array;
        const low = this.data.low_price_array;
        const close = this.data.close_price_array;
        const volume = this.data.volume_array;

        const volumeData = [];
        const ohlcData = [];
        for (let i = 0; i < date.length; i++) {
          ohlcData.push([
              date[i],
              open[i],
              high[i],
              low[i],
              close[i]
          ]);

          volumeData.push([
              date[i],
              volume[i]
          ]);
        }

        this.chartOptions = {
            chart: {
                height: 900,
            },

            title: {
                text: this.ticker + " Historical",
            },

            subtitle: {
              text: 'With SMA and Volume by Price technical indicators'
            },

            rangeSelector: {
              selected: 2
            },

            yAxis: [{
              startOnTick: false,
              endOnTick: false,
              labels: {
                  align: 'right',
                  x: -3
              },
              title: {
                  text: 'OHLC'
              },
              height: '60%',
              lineWidth: 2,
            }, {
              labels: {
                  align: 'right',
                  x: -3
              },
              title: {
                  text: 'Volume'
              },
              top: '65%',
              height: '35%',
              offset: 0,
              lineWidth: 2
            }],

            tooltip: {
              split: true
            },

            series: [{
              type: 'candlestick',
              name: this.ticker,
              id: this.ticker,
              data: ohlcData,
              zIndex: 2,
              lineWidth: 1.3
            }, {
              type: 'column',
              name: 'Volume',
              id: 'volume',
              data: volumeData,
              yAxis: 1
            }, {
              type: 'vbp',
              linkedTo: this.ticker,
              params: {
                volumeSeriesID: 'volume'
              },
              dataLabels: {
                enabled: false,
              },
              zoneLines: {
                enabled: false
              }
            }, {
              type: 'sma',
              linkedTo: this.ticker,
              zIndex: 1,
              marker: {
                enabled: false
              }
            }],

            responsive: {
              rules: [
                {
                  condition: {
                    maxWidth: 576
                  },
                  chartOptions: {
                    chart: {
                      height: 850
                    }
                  }
                }
              ]
            }
        }    
      }

}
