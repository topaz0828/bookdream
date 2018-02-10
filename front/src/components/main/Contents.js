import React from 'react';
import Header from './Header';
import Body from './Body';
import AddContentsModal from './AddContentsModal';
import DetailModal from './DetailModal';

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

	showDetailModal(info) {
		this.detailModal.show(info);
	}

	render() {
		return (
			<div>
				<Header app={this} moveMyPage={this.moveMyPage}/>
		        <Body range='all' ref={(ref) => {this.contentsView = ref;}} contents={this}/>
		        <AddContentsModal/>
		        <DetailModal ref={(ref) => {this.detailModal = ref;}}/>
			</div>
		);
	}
}

export default Contents;