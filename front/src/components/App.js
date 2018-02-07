import React from 'react';
import Header from './Header';
import Body from './Body';
import AddContentsModal from './AddContentsModal';
import MyPage from './mypage/MyPage';

class App extends React.Component {
	moveMyPage() {
		$('#mainView').hide();
		$('#myPage').show();
	}

	moveMainView() {
		$('#mainView').show();
		$('#myPage').hide();
	}

    render(){
        return (
        		<div>
        			<div id='mainView'>
		                <Header moveMyPage={this.moveMyPage}/>
		                <Body/>
		                <AddContentsModal/>
		            </div>
		            <div id='myPage' hidden='true'>
		            	<MyPage moveMainView={this.moveMainView}/>
		            </div>
	            </div>
        );
    }
}

export default App;
