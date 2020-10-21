import { Component, OnInit } from '@angular/core';
import { StockDataService } from '../stock-details/stock-data.service';


@Component({
    selector: 'watchlist',
    templateUrl: './watchlist.component.html',
    styles: [
        `
          h1 {
            font-weight: 600;
          }
        `
    ]
})

export class WatchlistComponent implements OnInit{
    watchList: Array<any>

    constructor(
        private stockDataService: StockDataService,
    ) {
        this.watchList = JSON.parse(localStorage.getItem("watchlist") || "[]" );
    }

    ngOnInit() {
        this.fetchLatestPrices();
    }

    fetchLatestPrices() {
        const tickers = [];
        for (let item of this.watchList) {
            tickers.push(item.ticker);
        }
        this.stockDataService.getLatestPrice(tickers).subscribe(
            (ob: Array<any>) => {
                ob.sort((x, y) => (x.ticker > y.ticker) ? 1 : -1);

                for (let i = 0; i < ob.length; i++) {
                    if (ob[i].error !== undefined) {
                        break;
                    }
                    this.watchList[i].last = ob[i].price;
                    this.watchList[i].change = ob[i].change;
                    this.watchList[i].changePercent = ob[i].changePercent;
                }
                localStorage.setItem('watchlist', JSON.stringify(this.watchList));
            }
        );
    }

    onCardDelete(ticker) {
        for (let i = 0; i < this.watchList.length; i++) {
            if (this.watchList[i].ticker == ticker) {
                this.watchList.splice(i, 1);
            }
        }
        localStorage.setItem('watchlist', JSON.stringify(this.watchList));
    }


}