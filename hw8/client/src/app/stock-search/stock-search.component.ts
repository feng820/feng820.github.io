import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from  '@angular/forms';
import { Router } from '@angular/router';
import * as _ from 'underscore';
import { StockDataService } from './stock-data.service';


@Component({
    selector: 'stock-search',
    templateUrl: './stock-search.component.html',
    styleUrls: ['./stock-search.component.css']
})

export class StockSearchComponent implements OnInit{
    form: FormGroup;
    searchResults: any;
    isLoading: boolean;

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private stockDataService: StockDataService,
    ){}

    ngOnInit() {
        this.form = this.formBuilder.group({
            ticker: this.formBuilder.control('')
        });
        this.search = _.debounce(this.search, 1000);
        this.isLoading = false;
    }

    search() {
        const input = this.form.get('ticker').value;
        if (input === '') {
            return;
        }
        this.isLoading = true;
        this.stockDataService.getStockSuggestions(input)
            .subscribe((data: any) => {
                this.isLoading = false;
                const filtered = data.filter(result => result.name !== null);
                this.searchResults = filtered;
            });
    }

    onSubmit(input) {
        this.router.navigate(['/details/', input.ticker])
    }
}
