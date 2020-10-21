import { Component, OnInit } from '@angular/core';
import { combineLatest } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { StockDataService } from './stock-data.service';
import { debounceTime } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { StockModalComponent } from '../stock-modal/stock-modal.component';


@Component({
    selector: 'stock-detail',
    templateUrl: './stock-detail.component.html',
    styleUrls: ['./stock-detail.component.css']
})

export class StockDetailComponent implements OnInit{
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
    

    constructor(
        private route: ActivatedRoute,
        private stockDataService: StockDataService,
        private modalService: NgbModal,
    ){
        this.isLoading = true;
        this.watchList = JSON.parse(localStorage.getItem("watchlist") || "[]" );
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

                this.stockDataService.getLastDayChartData(ticker, this.companySummary.timestamp.split(' ')[0])
                    .subscribe(ob5 => {
                        this.lastDayChartData = ob5;
                        this.hasError = hasError;
                        this.isLoading = false;
                    });
        });

        this._startAlertSubject.subscribe(message => this.afterStarMessage = message);
        this._startAlertSubject.pipe(
          debounceTime(5000)
        ).subscribe(() => this.afterStarMessage = '');

        this._buyAlertSubject.subscribe(message => this.afterBuyMessage = message);
        this._buyAlertSubject.pipe(
          debounceTime(5000)
        ).subscribe(() => this.afterBuyMessage = '');


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

    open() {
        const modalRef = this.modalService.open(StockModalComponent);
        modalRef.componentInstance.title = this.companyOutlook.ticker;
        modalRef.componentInstance.price = this.companySummary.last;
        modalRef.result.then(result => {
            if (result === 'buy') {
                this.changeBuyAlertMessage();
            }
        })
    }
}