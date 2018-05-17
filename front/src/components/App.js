import React from 'react';
import { Route } from 'react-router-dom';

import Contents from './main/Contents'
import MyPage from './mypage/MyPage';
import DetailModal from './DetailModal';
import AddContentsModal from './AddContentsModal';
import SignInModal from './SignInModal';

class App extends React.Component {
	moveMyPage() {
		this.checkJoinStatus(() => {document.location = "/mypage";});
	}

	moveMainView() {
		document.location = "/";
	}

	showDetailModal(info) {
		this.detailModal.show(info);
	}

	showAddModal() {
		this.checkJoinStatus(() => this.addModal.show());
	}

	checkJoinStatus(callback) {
		var self =  this;
		$.ajax({
			type: 'GET',
			url: '/api/user/login-status'
		}).done(function() {
			callback();
		}).fail(function(res) {
			if (res.status == 401) {
				self.showSignInModal();
			}
		})
	}

	showSignInModal() {
		this.refs.signInModal.show();
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
				<AddContentsModal ref={(ref) => {this.addModal = ref;}} app={this}/>
				<SignInModal ref='signInModal'/>
			</div>
        );
    }
}

export default App;
