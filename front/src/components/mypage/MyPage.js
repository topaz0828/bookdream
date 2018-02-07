import React from 'react';
import Header from './Header';

class MyPage extends React.Component {
	constructor(props) {
		super(props);
		this.moveMainView = props.moveMainView;
	}

    render(){
        return (
        		<div>
        			<Header moveMainView={this.moveMainView}/>
	            </div>
        );
    }
}

export default MyPage;
