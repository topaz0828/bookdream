import React from 'react';
import Contents from './main/Contents'
import MyPage from './mypage/MyPage';
import DetailModal from './DetailModal';

class App extends React.Component {
	componentDidMount() {
		this.mainViewDiv = $('#mainView');
		this.myPageDiv = $('#myPage');
	}

	moveMyPage() {
		this.mainViewDiv.hide();
		this.myPageDiv.show();
		this.myPage.getInfo();
	}

	moveMainView() {
		this.mainViewDiv.show();
		this.myPageDiv.hide();
	}

	showDetailModal(info) {
		this.detailModal.show(info);
	}

    render(){
        return (
        		<div className='container-fluid'>
        			<div id='mainView'>
		                <Contents app={this} ref={(ref) => {this.contents = ref;}}/>
		            </div>
		            <div id='myPage' hidden='true'>
		            	<MyPage app={this} ref={(ref) => {this.myPage = ref;}}/>
		            </div>
		            <DetailModal ref={(ref) => {this.detailModal = ref;}}/>
	            </div>
        );
    }
}

export default App;
