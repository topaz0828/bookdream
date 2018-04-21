import React from 'react';

class DetailModal extends React.Component {
	constructor(props) {
		super(props);
		this.state = {info: {title: '', nickname: ''}, contents: ''};
	}

	componentDidMount() {
		this.modal = $('#detailModal');
		this.detailText = $('#detailText');
		this.modifyTextarea = $('#modifyTextarea');
		this.detailModifyButton = $('#detailModifyButton');
		this.detailSaveButton = $('#detailSaveButton');
		this.detailNoti = $('#detailNoti');
		this.detailSaveButton.hide();
		this.modifyTextarea.hide();
	}

	show(info) {
		var self = this;
		$.ajax({
			type: 'GET',
			url: '/api/search/contents?id=' + info.contentsId,
			dataType: 'json'
		}).done(function(res) {
			self.setState({info: info, contents: res.TEXT});

			self.detailText.show();
			self.modifyTextarea.hide();
			self.detailSaveButton.hide();
			self.modifyTextarea.val('');
			self.detailNoti.text('');

			if ('y' === info.my) {
				self.detailModifyButton.show();
			} else {
				self.detailModifyButton.hide();
			}

			if ('C' === info.type) {
				self.modifyTextarea.attr('maxLength', '200');
			} else {
				self.modifyTextarea.removeAttr('maxLength');
			}
			self.modal.modal();
		}).fail(function(err) {
			alert('server error');
		});
	}

	showModify() {
		this.detailText.hide();
		this.modifyTextarea.show();
		this.modifyTextarea.val(this.state.contents);
		this.detailSaveButton.show();
		this.detailModifyButton.hide();
	}

	save() {
		var contents = this.modifyTextarea.val();
		if (contents.length == 0) {
			return;
		}

		var data = {bookId: this.state.info.bookId, 
				type: this.state.info.type, 
				contentsId: this.state.info.contentsId, 
				contents: contents};

		var self = this;
		$.ajax({
			type: 'PUT',
			url: '/api/contents/update',
			data: JSON.stringify(data),
		}).done(function(res) {
			self.modal.modal('hide');
			self.state.info.parent.refresh();
		}).fail(function(data) {
			self.detailNoti.text('저장되지 않았습니다. 다시 시도해 주세요.');
		});
	}

	render() {
		return (
			<div className="modal fade" id="detailModal" tabIndex="-1" role="dialog" aria-labelledby="detailModalLabel" aria-hidden="true">
				<div className="modal-dialog">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 className="modal-title" id="detailModalLabel">{this.state.info.title}</h4>
						</div>
						<div className="modal-body">
							<div id='detailText'>
								<p>{this.state.contents}</p>
								<div align='right'>- {this.state.info.nickname} -</div>
							</div>
							<textarea id='modifyTextarea' className='form-control' style={{height:'200px', resize:'none'}}></textarea>
							<div align='right' style={{paddingTop: '10px'}} id='detailNoti'></div> 
						</div>
						<div className="modal-footer">
							<button id='detailModifyButton' type="button" className='btn btn-default' onClick={() => this.showModify()}>Modify</button>
							<button id='detailSaveButton' type="button" className='btn btn-default' onClick={() => this.save()}>Save</button>
							<button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default DetailModal;