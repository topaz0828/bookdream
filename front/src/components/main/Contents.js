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
		this.contentsView.getList();
	}

	getList() {
		this.contentsView.getList();
	}

	render() {
		return (
			<div>
				<Header app={this} moveMyPage={this.moveMyPage}/>
		        <Body range='all' ref={(ref) => {this.contentsView = ref;}}/>
		        <AddContentsModal/>
			</div>
		);
	}
}

export default Contents;