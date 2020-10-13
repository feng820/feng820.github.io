import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StockSearchComponent } from './stock-search/stock-search.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { PortfolioComponent } from './portfolio/portfolio.component';

const routes: Routes = [
  { path: '', component: StockSearchComponent, pathMatch: 'full' },
  { path: 'watchlist', component: WatchlistComponent},
  { path: 'portfolio', component: PortfolioComponent},
  // { path: ''},
  // { path: 'details/:ticker'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
