import React from 'react';
import Header from './Header';
import State from './State';
import Body from '../main/Body';

class MyPage extends React.Component {
	constructor(props) {
		super(props);
        this.app = props.app;
		this.moveMainView = () => {
            this.app.moveMainView();
        }
	}

	getInfo() {
		this.myState.getState();
        this.myContents.refresh();
	}

    render(){
        return (
    		<div>
    			<Header moveMainView={this.moveMainView}/>
    			<State ref={(ref) => {this.myState = ref;}}/>
    			<Body range='my' ref={(ref) => {this.myContents = ref;}}/>
            </div>
        );
    }
}

export default MyPage;
