import { Component, OnInit } from '@angular/core';
import { StockDataService } from '../stock-details/stock-data.service';


@Component({
    selector: 'portfolio',
    templateUrl: './portfolio.component.html',
    styles: [
        `
          h1 {
            font-weight: 600;
          }
        `
    ]
})

export class PortfolioComponent implements OnInit{
    portfolio: Array<any>

    constructor(
      private stockDataService: StockDataService,
    ) {
        this.portfolio = JSON.parse(localStorage.getItem("portfolio") || "[]" );
    }

    ngOnInit() {
      this.fetchLatestPrices();
    }

    private round(num) {
      return Math.round((num + Number.EPSILON) * 1000) / 1000;
    }

    fetchLatestPrices() {
      const tickers = [];
      for (let item of this.portfolio) {
          tickers.push(item.ticker);
      }
      this.stockDataService.getLatestPrice(tickers).subscribe(
          (ob: Array<any>) => {
              ob.sort((x, y) => (x.ticker > y.ticker) ? 1 : -1);

              for (let i = 0; i < ob.length; i++) {
                  if (ob[i].error !== undefined) {
                      break;
                  }
                  const portfolioInfo = this.portfolio[i];
                  portfolioInfo.currentPrice = ob[i].price;
                  portfolioInfo.change = this.round(portfolioInfo.currentPrice - portfolioInfo.avgCostPerShare);
                  portfolioInfo.marketValue = this.round(portfolioInfo.currentPrice * portfolioInfo.quantity);
              }
              localStorage.setItem('watchlist', JSON.stringify(this.portfolio));
          }
      );
    }

    onHandleUpdatePrice() {
        this.fetchLatestPrices();
    }

    
}