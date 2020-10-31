import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import  { ReactiveFormsModule }  from  '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { HttpClientModule } from '@angular/common/http';
import { MatTabsModule } from '@angular/material/tabs';
import { HighchartsChartModule } from 'highcharts-angular';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { StockSearchComponent } from './stock-search/stock-search.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { StockDetailComponent } from './stock-details/stock-detail.component';
import { SummaryChartComponent } from './stock-details/chart/summary-chart.component';
import { NewsCardComponent } from './stock-details/news-card/news-card.component';
import { TabChartComponent } from './stock-details/chart/tab-chart.component';
import { WatchlistCardComponent } from './watchlist/watchlist-card/watchlist-card.component';
import { StockModalComponent } from './stock-modal/stock-modal.component';
import { PortfolioCardComponent } from './portfolio/portfolio-card/portfolio-card.component';
import { NewsModalComponent } from './stock-details/news-modal/news-modal.component';
import { DisplayNumberPipe } from './display-number.pipe';
import { DisplayDatePipe } from './display-date.pipe';

// configure the classes you decorate as angular module (Metadata)
@NgModule({
  declarations: [ // make components, directives and pipes available to your module
    AppComponent,
    StockSearchComponent,
    WatchlistComponent,
    PortfolioComponent,
    StockDetailComponent,
    SummaryChartComponent,
    NewsCardComponent,
    TabChartComponent,
    WatchlistCardComponent,
    StockModalComponent,
    PortfolioCardComponent,
    NewsModalComponent,
    DisplayNumberPipe,
    DisplayDatePipe
  ],
  imports: [ // bring in other modules that you module will need
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatProgressSpinnerModule,
    HttpClientModule,
    MatTabsModule,
    HighchartsChartModule,
    NgbModule,
  ],
  providers: [
    DisplayDatePipe
  ],
  bootstrap: [ // entry point for you app code
    AppComponent
  ] 
})
export class AppModule { }
