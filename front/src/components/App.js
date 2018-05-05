import React from 'react';
import { Route } from 'react-router-dom';

import Contents from './main/Contents'
import MyPage from './mypage/MyPage';
import DetailModal from './DetailModal';
import AddContentsModal from './AddContentsModal';

class App extends React.Component {
	moveMyPage() {
		document.location = "/mypage";
	}

	moveMainView() {
		document.location = "/";
	}

	showDetailModal(info) {
		this.detailModal.show(info);
	}

	refresh() {
		if (this.contents) {
			this.contents.refresh();
		} else if (this.myPage) {
			this.myPage.refresh();
		}
	}

    render(){
        return (
			<div className='container-fluid'>
				<Route exact path="/" component={(props) => ( <Contents app={this} ref={(ref) => {this.contents = ref;}}/>)}/>
                <Route path="/mypage" component={(props) => ( <MyPage app={this} ref={(ref) => {this.myPage = ref;}}/>)}/>
				<DetailModal ref={(ref) => {this.detailModal = ref;}} app={this}/>
				<AddContentsModal app={this}/>
			</div>
        );
    }
}

export default App;
