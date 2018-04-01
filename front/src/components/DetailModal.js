import React from 'react';

class DetailModal extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			text: '',
			title: '',
			author: '',
			nickname: ''
		};
	}

	componentDidMount() {
		this.modal = $('#detailModal');
	}

	show(info) {
		//서버와 통신에서 내용을 가져온다.
		this.setState({
			text: info.text,
			title: info.title,
			author: info.author,
			nickname: info.nickname
		});
		this.modal.modal();
	}

	render() {
		return (
			<div className="modal fade" id="detailModal" tabIndex="-1" role="dialog" aria-labelledby="detailModalLabel" aria-hidden="true">
				<div className="modal-dialog">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 className="modal-title" id="detailModalLabel">{this.state.title}</h4>
						</div>
						<div className="modal-body">
							<p>{this.state.text}</p>
							<div align='right'>- {this.state.nickname} -</div>
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default DetailModal;