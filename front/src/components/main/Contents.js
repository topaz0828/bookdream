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
		this.body.getListBySearchInput('');
	}

	getList(query) {
		this.body.getListBySearchInput(query);
	}

	showDetailModal(info) {
		this.detailModal.show(info);
	}

	render() {
		return (
			<div>
				<Header moveMyPage={this.moveMyPage} contents={this}/>
		        <Body range='all' ref={(ref) => {this.body = ref;}} contents={this}/>
		        <AddContentsModal/>
		        <DetailModal ref={(ref) => {this.detailModal = ref;}}/>
			</div>
		);
	}
}

export default Contents;