import { Component, OnInit, OnDestroy } from '@angular/core';
import { combineLatest, Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { StockDataService } from './stock-data.service';
import { debounceTime } from 'rxjs/operators';
import { Subject, interval } from 'rxjs';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { StockModalComponent } from '../stock-modal/stock-modal.component';
import { DisplayDatePipe } from '../display-date.pipe';


@Component({
    selector: 'stock-detail',
    templateUrl: './stock-detail.component.html',
    styleUrls: ['./stock-detail.component.css']
})

export class StockDetailComponent implements OnInit, OnDestroy {
    isLoading: boolean;
    hasError: boolean;
    isInWatchList: boolean;
    companyOutlook: any;
    companySummary: any;
    historyChartData: any;
    lastDayChartData: any;
    newsDataArray: any;
    afterStarMessage: string;
    afterBuyMessage: string;
    private _startAlertSubject = new Subject<string>();
    private _buyAlertSubject = new Subject<string>();
    watchList: Array<any>;
    portfolio: Array<any>;
    updateSubscription: Subscription;
    buyModalRef: NgbModalRef;


    constructor(
        private route: ActivatedRoute,
        private stockDataService: StockDataService,
        private modalService: NgbModal,
        private datePipe: DisplayDatePipe,
    ){
        this.isLoading = true;
        this.watchList = JSON.parse(localStorage.getItem("watchlist") || "[]" );
        this.portfolio = JSON.parse(localStorage.getItem("portfolio") || "[]" );
    }

    ngOnInit() {
        let ticker: String;
        this.route.paramMap.subscribe(paramMap => {
            ticker = paramMap.get('ticker');
        });

        if (ticker === null || ticker.length === 0) {
            this.hasError = true;
            this.isLoading = false;
            return;
        }

        const ob1 = this.stockDataService.getCompanyOutLook(ticker);
        const ob2 = this.stockDataService.getCompanySummary(ticker);
        const ob3 = this.stockDataService.getHistoryChartData(ticker);
        const ob4 = this.stockDataService.getNewsData(ticker);

        combineLatest([ob1, ob2, ob3, ob4]).subscribe(([ob1, ob2, ob3, ob4]) => {
                this.companyOutlook = ob1;
                
                const hasError = 'error' in this.companyOutlook;
                if (hasError) {
                    this.hasError = hasError;
                    this.isLoading = false;
                    return;
                }
                this.companySummary = ob2;
                this.historyChartData = ob3;
                this.newsDataArray = ob4;
                this.isInWatchList = this.isTickerInWatchlist(this.companyOutlook.ticker);

                const pipedDate = this.datePipe.transform(this.companySummary.timestamp);
                if (pipedDate !== null) {
                    this.stockDataService.getLastDayChartData(ticker, pipedDate.split(' ')[0])
                    .subscribe(ob5 => {
                        this.lastDayChartData = ob5;
                        this.hasError = hasError;
                        this.isLoading = false;
                    });
                } else {
                    this.lastDayChartData = null;
                    this.hasError = hasError;
                    this.isLoading = false;
                }
        });

        this._startAlertSubject.subscribe(message => this.afterStarMessage = message);
        this._startAlertSubject.pipe(
          debounceTime(5000)
        ).subscribe(() => this.afterStarMessage = '');

        this._buyAlertSubject.subscribe(message => this.afterBuyMessage = message);
        this._buyAlertSubject.pipe(
          debounceTime(5000)
        ).subscribe(() => this.afterBuyMessage = '');

        this.updateSubscription = interval(15 * 1000).subscribe(() => {
            if (!this.hasError && this.companySummary.marketOpen) {
                this.stockDataService.getCompanySummary(ticker).subscribe(
                    ob1 => {
                        this.companySummary = ob1;
                        const pipedDate = this.datePipe.transform(this.companySummary.timestamp);
                        if (pipedDate !== null) {
                            this.stockDataService.getLastDayChartData(ticker, pipedDate.split(' ')[0])
                            .subscribe(ob2 => {
                                this.lastDayChartData = ob2;
                                if (this.modalService.hasOpenModals() && this.buyModalRef !== undefined) {
                                    this.buyModalRef.componentInstance.price = this.companySummary.last;
                                }
                            });
                        }
                    }
                )
            }
        });

    }

    ngOnDestroy() {
        if (this.updateSubscription !== undefined) {
            this.updateSubscription.unsubscribe();
        }
    }

    isTickerInWatchlist(ticker) {
        for (let tickerInfo of this.watchList) {
            if (tickerInfo.ticker == ticker) {
                return true;
            }
        }
        return false;
    }

    addTickerToWatchlist(tickerInfo) {
        this.watchList.push(tickerInfo);
        this.watchList.sort((x, y) => (x.ticker > y.ticker) ? 1 : -1);
        localStorage.setItem('watchlist', JSON.stringify(this.watchList));
    }

    removeTickerFromWatchlist(ticker) {
        for (let i = 0; i < this.watchList.length; i++) {
            if (this.watchList[i].ticker == ticker) {
                this.watchList.splice(i, 1);
            }
        }
        localStorage.setItem('watchlist', JSON.stringify(this.watchList));
    }

    onClickStar() {
        if (!this.isInWatchList) {
            this.addTickerToWatchlist({
                'ticker': this.companyOutlook.ticker,
                'name': this.companyOutlook.name,
                'change': this.companySummary.change,
                'changePercent': this.companySummary.changePercent,
                'last': this.companySummary.last
            });
        } else {
            this.removeTickerFromWatchlist(this.companyOutlook.ticker);
        }
        this.isInWatchList = !this.isInWatchList;
        this.changeStarAlertMessage();
    }

    changeStarAlertMessage() {
        const message = this.isInWatchList ? " added to Watchlist." : " removed from Watchlist.";
        this._startAlertSubject.next(this.companyOutlook.ticker + message);
    }

    changeBuyAlertMessage() {
        const message = this.companyOutlook.ticker + " bought successfully!";
        this._buyAlertSubject.next(message);
    }

    isTickerInPortfolio(ticker) {
        let tickerInfo;
        for (let i = 0; i < this.portfolio.length; i++) {
            tickerInfo = this.portfolio[i];
            if (tickerInfo.ticker == ticker) {
                return i;
            }
        }
        return -1;
    }

    updatePortfolio(quantity) {
        const index = this.isTickerInPortfolio(this.companyOutlook.ticker);
        const currentPrice = this.companySummary.last;
        const currentCost = currentPrice * quantity;
        if (index === -1) {
            this.portfolio.push({
                'ticker': this.companyOutlook.ticker,
                'name': this.companyOutlook.name,
                'quantity': quantity,
                'totalCost': currentCost,
                'avgCostPerShare': currentCost/quantity,
                'currentPrice': currentPrice,
                'change': currentPrice - currentCost/quantity,
                'marketValue': currentPrice * quantity,
                'boughtPrice': currentPrice
            });
            this.portfolio.sort((x, y) => (x.ticker > y.ticker) ? 1 : -1);

        } else {
            const portfolioInfo = this.portfolio[index];
            portfolioInfo.quantity += quantity;
            portfolioInfo.totalCost += currentCost;
            portfolioInfo.totalCost = portfolioInfo.totalCost;
            portfolioInfo.avgCostPerShare = portfolioInfo.totalCost / portfolioInfo.quantity;

            portfolioInfo.currentPrice = currentPrice;
            portfolioInfo.change = currentPrice - portfolioInfo.avgCostPerShare;
            portfolioInfo.marketValue = currentPrice * portfolioInfo.quantity;
        }

        localStorage.setItem('portfolio', JSON.stringify(this.portfolio));
    }

    open() {
        this.buyModalRef =  this.modalService.open(StockModalComponent);
        this.buyModalRef.componentInstance.title = this.companyOutlook.ticker;
        this.buyModalRef.componentInstance.price = this.companySummary.last;
        this.buyModalRef.componentInstance.isBuy = true;
        this.buyModalRef.result.then(quantity => {
            if (Number.isInteger(quantity)) {
                this.changeBuyAlertMessage();
                this.updatePortfolio(quantity);
                this.buyModalRef = undefined;
            }
        }, reject => {})
    }
}
