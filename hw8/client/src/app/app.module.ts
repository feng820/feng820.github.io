import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { StockSearchComponent } from './stock-search/stock-search.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { PortfolioComponent } from './portfolio/portfolio.component';

// configure the classes you decorate as angular module (Metadata)
@NgModule({
  declarations: [ // make components, directives and pipes available to your module
    AppComponent,
    StockSearchComponent,
    WatchlistComponent,
    PortfolioComponent
  ],
  imports: [ // bring in other modules that you module will need
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [ // entry point for you app code
    AppComponent
  ] 
})
export class AppModule { }
