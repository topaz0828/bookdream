import React from 'react';
import Header from './Header';
import State from './State';
import ModifyModal from './ModifyModal';
import Body from '../main/Body';

class MyPage extends React.Component {
	constructor(props) {
		super(props);
        this.app = props.app;
		this.moveMainView = () => {
            this.app.moveMainView();
        }
    }
    
    componentDidMount() {
        this.refresh();
    }

    refresh() {
        this.myState.getState();
        this.myContents.refresh();
    }
    
    modifyMyInfo() {
        this.modifyModal.show(this.myState.getMyInfo());
    }

    render(){
        return (
    		<div align='center'>
    			<Header moveMainView={this.moveMainView} mypage={this}/>
    			<State ref={(ref) => {this.myState = ref;}}/>
    			<Body range='my' ref={(ref) => {this.myContents = ref;}} parent={this}/>
                <ModifyModal ref={(ref) => {this.modifyModal = ref;}} mypage={this} />
            </div>
        );
    }
}

export default MyPage;
