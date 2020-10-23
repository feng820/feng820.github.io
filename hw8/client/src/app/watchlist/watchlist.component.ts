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
    watchList: Array<any>;
    isLoading: boolean;

    constructor(
        private stockDataService: StockDataService,
    ) {
        this.watchList = JSON.parse(localStorage.getItem("watchlist") || "[]" );
        this.isLoading = true;
    }

    ngOnInit() {
        this.fetchLatestPrices();
    }

    fetchLatestPrices() {
        const tickers = [];
        for (let item of this.watchList) {
            tickers.push(item.ticker);
        }
        if (tickers.length === 0) {
            this.isLoading = false;
            return;
        }
        this.stockDataService.getLatestPrice(tickers).subscribe(
            (ob: Array<any>) => {
                ob.sort((x, y) => (x.ticker > y.ticker) ? 1 : -1);

                for (let i = 0; i < ob.length; i++) {
                    if (ob[i].error !== undefined) {
                        break;
                    }
                    const watchlistItem = this.watchList[i]
                    watchlistItem.last = ob[i].price;
                    watchlistItem.change = ob[i].change;
                    watchlistItem.changePercent = ob[i].changePercent;
                }
                localStorage.setItem('watchlist', JSON.stringify(this.watchList));
                this.isLoading = false;
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
        this.fetchLatestPrices();
    }


}
