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
        const url = `http://localhost:3000/search/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getCompanyOutLook(ticker) {
        const url = `http://localhost:3000/outlook/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getCompanySummary(ticker) {
        const url = `http://localhost:3000/summary/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getHistoryChartData(ticker) {
        const url = `http://localhost:3000/history/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getLastDayChartData(ticker, lastTimeStamp) {
        const options = {
            params: {lastTimeStamp: lastTimeStamp}
        };
        const url = `http://localhost:3000/last/${ticker}`
        return this.http.get(url, options)
            .pipe(catchError(this.handleError));
    }

    getNewsData(ticker) {
        const url = `http://localhost:3000/news/${ticker}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    getLatestPrice(tickers) {
        const url = `http://localhost:3000/price/${tickers}`
        return this.http.get(url)
            .pipe(catchError(this.handleError));
    }

    private handleError(error: HttpErrorResponse) {
        console.log(error.message);
        return throwError("Cannot fetch data");
    }
}