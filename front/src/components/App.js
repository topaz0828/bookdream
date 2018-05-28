import React from 'react';
import { Route } from 'react-router-dom';

import Contents from './main/Contents'
import MyPage from './mypage/MyPage';
import DetailModal from './DetailModal';
import AddContentsModal from './AddContentsModal';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';

class App extends React.Component {
	componentDidMount() {
		window.addEventListener("hashchange", () => {this.checkHash()}, false);
		$(document).ready(() => {this.checkHash()});
	}

	checkHash() {
		if (window.location.hash) {
			var hash = window.location.hash.split('#');
			if (hash.length > 1) {
				var data = hash[1].split('&');
				if (data.length > 0) {
					if (data[0] === 't=c') { // type=contents
						this.shwDetailViewByHash(data[1]);
					} else if (data[0] === 't=su') { // type=contents
						this.showSignUpModal(data);
					}
				}
			}
		}
	}

	moveMyPage() {
		this.checkJoinStatus(() => {document.location = "/mypage";});
	}

	moveMainView() {
		document.location = "/";
	}

	shwDetailViewByHash(data) {
		var id = data.split('=');
		if (id.length > 1 && id[0] === 'i') { // id=contentsId
			this.showDetailModal(id[1]);
		}
	}

	showDetailModal(contentsId) {
		this.detailModal.show(contentsId);
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

	showSignUpModal(data) {
		this.refs.signUpModal.show(data);
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
				<SignUpModal ref='signUpModal'/>
			</div>
        );
    }
}

export default App;
