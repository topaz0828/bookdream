import React from 'react';
import ReactDOM from 'react-dom';
import Root from './Root';

export { default as Contents } from './components/main/Contents';
export { default as MyPage } from './components/mypage/MyPage';

const rootElement = document.getElementById('root');
ReactDOM.render(<Root/>, rootElement);
