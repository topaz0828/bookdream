import React from 'react';
import Header from './Header';
import Body from './Body';

class Contents extends React.Component {
	constructor(props) {
		super(props);
		this.app = props.app;
		this.moveMyPage = () => {
			this.app.moveMyPage();
		}
	}

	componentDidMount() {
		this.body.getListBySearchInput('');
	}

	getList(query) {
		this.body.getListBySearchInput(query);
	}

	refresh() {
		this.body.refresh();
	}

	render() {
		return (
			<div align='center'>
				<Header moveMyPage={this.moveMyPage} contents={this}/>
		        <Body range='all' ref={(ref) => {this.body = ref;}} parent={this}/>
			</div>
		);
	}
}

export default Contents;