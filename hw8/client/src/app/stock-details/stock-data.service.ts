import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})

export class StockDataService {
    constructor(private http: HttpClient) {}

    getStockSuggestions(ticker) {
        const url = `/api/search/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getCompanyOutLook(ticker) {
        const url = `/api/outlook/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getCompanySummary(ticker) {
        const url = `/api/summary/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getHistoryChartData(ticker) {
        const url = `/api/history/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getLastDayChartData(ticker, lastTimeStamp) {
        const options = {
            params: {lastTimeStamp: lastTimeStamp}
        };
        const url = `/api/last/${ticker}`
        return this.http.get(url, options)
            .pipe(catchError(this.handleError));
    }

    getNewsData(ticker) {
        const url = `/api/news/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getLatestPrice(tickers) {
        const url = `/api/price/${tickers}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    private handleError(error: HttpErrorResponse) {
        console.log(error.message);
        return throwError("Cannot fetch data");
    }
}