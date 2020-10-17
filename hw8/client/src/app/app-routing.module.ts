import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StockSearchComponent } from './stock-search/stock-search.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { StockDetailComponent } from './stock-details/stock-detail.component';

const routes: Routes = [
  { path: '', component: StockSearchComponent, pathMatch: 'full'},
  { path: 'details/:ticker', component: StockDetailComponent },
  { path: 'watchlist', component: WatchlistComponent },
  { path: 'portfolio', component: PortfolioComponent },
  { path: '**', redirectTo: '/'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
