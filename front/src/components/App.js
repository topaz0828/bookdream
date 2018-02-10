import React from 'react';
import Contents from './main/Contents'
import MyPage from './mypage/MyPage';

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

    render(){
        return (
        		<div className='container-fluid'>
        			<div id='mainView'>
		                <Contents app={this} ref={(ref) => {this.contents = ref;}}/>
		            </div>
		            <div id='myPage' hidden='true'>
		            	<MyPage app={this} ref={(ref) => {this.myPage = ref;}}/>
		            </div>
	            </div>
        );
    }
}

export default App;
