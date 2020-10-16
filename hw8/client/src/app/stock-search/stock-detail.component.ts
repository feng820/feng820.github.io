import { Component, OnInit } from '@angular/core';
import { combineLatest} from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { StockDataService } from './stock-data.service';
import { MatTabsModule } from '@angular/material/tabs';


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
        // const ob3 = this.stockDataService.getHistoryChartData(ticker);
        // const ob4 = this.stockDataService.getLastDayChartData(ticker);

        combineLatest([ob1, ob2]).subscribe(([ob1, ob2]) => {
                this.companyOutlook = ob1;
                this.companySummary = ob2;
                // this.historyChartData = ob3;
                // this.lastDayChartData = ob4;
                this.hasError = 'error' in this.companyOutlook;
                this.isLoading = false;
        });
        
    }

    onClickStar() {
        // add to local storage
        this.isInWatchList = !this.isInWatchList;
    }
}