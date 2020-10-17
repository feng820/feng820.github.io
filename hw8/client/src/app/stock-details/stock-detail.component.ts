import { Component, OnInit } from '@angular/core';
import { combineLatest } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { StockDataService } from './stock-data.service';


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

    constructor(
        private route: ActivatedRoute,
        private stockDataService: StockDataService,
    ){
        this.isLoading = true;
        this.isInWatchList = false; // fetch from local storage
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
        // const ob4 = this.stockDataService.getLastDayChartData(ticker, '2020-10-16');

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
                // this.lastDayChartData = ob4;
                // this.hasError = 'error' in this.companyOutlook;
                // this.isLoading = false;
                this.stockDataService.getLastDayChartData(ticker, this.companySummary.timestamp.split(' ')[0])
                    .subscribe(ob5 => {
                        this.lastDayChartData = ob5;
                        this.hasError = hasError;
                        this.isLoading = false;
                    })
        });
    }

    onClickStar() {
        // add to local storage
        this.isInWatchList = !this.isInWatchList;
    }
}