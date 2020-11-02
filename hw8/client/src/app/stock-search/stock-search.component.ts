import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from  '@angular/forms';
import { Router } from '@angular/router';
import { debounceTime } from 'rxjs/operators';
import { StockDataService } from '../stock-details/stock-data.service';
import { Subject } from 'rxjs';


@Component({
    selector: 'stock-search',
    templateUrl: './stock-search.component.html',
    styleUrls: ['./stock-search.component.css']
})

export class StockSearchComponent implements OnInit{
    form: FormGroup;
    searchResults: any;
    isLoading: boolean;
    subject = new Subject<string>();

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private stockDataService: StockDataService,
    ){}

    ngOnInit() {
        this.form = this.formBuilder.group({
            ticker: this.formBuilder.control('')
        });
        this.isLoading = false;
        this.subject.pipe(
            debounceTime(1000),
        ).subscribe(input => {
            this.isLoading = true;
            if (input === '') {
                this.isLoading = false;
                this.searchResults = [];
            } else {
                this.stockDataService.getStockSuggestions(input)
                .subscribe((data: any) => {
                    this.isLoading = false;
                    const filtered = data.filter(result => result.name !== null);
                    this.searchResults = filtered;
                });
            }
        })
    }

    search() {
        const input = this.form.get('ticker').value;
        this.subject.next(input);
    }

    onSubmit(input) {
        this.router.navigate(['/details/', input.ticker])
    }
}
