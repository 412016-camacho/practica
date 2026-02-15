import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('../components/world-list/world-list.component')
      .then(m => m.WorldListComponent)
  },
  {
    path: 'new-game',
    loadComponent: () => import('../components/new-game/new-game.component')
      .then(m => m.NewGameComponent)
  },
  {
    path: 'game/:gameId',
    loadComponent: () => import('../components/game-view/game-view.component')
      .then(m => m.GameViewComponent)
  },
  {
    path: 'ranking',
    loadComponent: () => import('../components/ranking/ranking.component')
      .then(m => m.RankingComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
