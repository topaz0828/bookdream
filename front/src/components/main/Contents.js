import React from 'react';
import Header from './Header';
import Body from './Body';
import AddContentsModal from './AddContentsModal';

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

	render() {
		return (
			<div>
				<Header moveMyPage={this.moveMyPage} contents={this}/>
		        <Body range='all' ref={(ref) => {this.body = ref;}} parent={this}/>
		        <AddContentsModal/>
			</div>
		);
	}
}

export default Contents;