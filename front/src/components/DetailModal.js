import React from 'react';
import copy from 'copy-to-clipboard';

class DetailModal extends React.Component {
	constructor(props) {
		super(props);
		this.state = {title: '', nickname: '', profileImage:'', contents: ''};
	}

	componentDidMount() {
		this.modal = $('#detailModal');
		this.detailText = $('#detailText');
		this.modifyTextarea = $('#modifyTextarea');
		this.detailModifyButton = $('#detailModifyButton');
		this.detailSaveButton = $('#detailSaveButton');
		this.detailNoti = $('#detailNoti');
		this.deleteContentsButton = $('#deleteContentsButton');
		this.confirmDeleteButton = $('#confirmDeleteButton');
		this.detailSaveButton.hide();
		this.modifyTextarea.hide();
		this.deleteContentsButton.hide();
		this.confirmDeleteButton.hide();
		this.reviewView = $('#reviewView');
		this.impressionView = $('#impressionView');
	}

	show(contentsId) {
		this.detailText.show();
		this.modifyTextarea.hide();
		this.detailSaveButton.hide();
		this.confirmDeleteButton.hide();
		this.detailModifyButton.hide();
		this.deleteContentsButton.hide();
		this.modifyTextarea.val('');
		this.reviewView.hide();
		this.impressionView.hide();
		this.detailNoti.text('');

		var self = this;
		$.ajax({
			type: 'GET',
			url: '/api/search/contents?id=' + contentsId,
			dataType: 'json'
		}).done(function(res) {
			setTimeout(() => self.setState({title: res.TITLE, 
				nickname: res.NICKNAME, 
				profileImage:res.PROFILE_IMAGE, 
				contents: res.TEXT,
				contentsId: contentsId,
				bookId: res.BOOK_ID,
				type: res.TYPE}));
			
			if ('y' === res.MY) {
				self.detailModifyButton.show();
				self.deleteContentsButton.show();
			}

			if ('C' === res.TYPE) {
				self.impressionView.html('<p>' + res.TEXT.replace(/(?:\r\n|\r|\n)/g, '<br>') + '</p>');
				self.impressionView.show();
				self.modifyTextarea.attr('maxLength', '200');
			} else {
				self.reviewView.val(res.TEXT);
				self.reviewView.show();
				self.modifyTextarea.removeAttr('maxLength');
			}
			self.modal.modal({backdrop: 'static'});
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

		var data = {bookId: this.state.bookId, 
				type: this.state.type, 
				contentsId: this.state.contentsId, 
				contents: contents};

		var self = this;
		$.ajax({
			type: 'PUT',
			url: '/api/contents/update',
			data: JSON.stringify(data),
			async: false
		}).done(function(res) {
			self.modal.modal('hide');
			self.props.app.refresh();
		}).fail(function(data) {
			self.detailNoti.text('저장되지 않았습니다. 다시 시도해 주세요.');
		});
	}

	delete() {
		var self = this;
		$.ajax({
			type: 'DELETE',
			url: '/api/contents/delete?contentsId=' + this.state.contentsId,
			async: false
		}).done(function(res) {
			self.modal.modal('hide');
			self.props.app.refresh();
		}).fail(function(data) {
			self.detailNoti.text('삭제하지 못 했습니다.');
		});
	}

	changeConfirm() {
		this.deleteContentsButton.hide();
		this.confirmDeleteButton.show();
	}

	saveToClipboardContentsUrl() {
		// copy("https://book.hellobro.win#t=c&i=" + this.state.contentsId);
		copy('http://localhost:3003#t=c&i=' + this.state.contentsId);
		this.detailNoti.text('클립보드에 저장되었습니다.');
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
							<div id='detailText'>
								<textarea id='reviewView' className='form-control' style={{height:'300px', resize:'none', cursor:'default'}} readOnly></textarea>
								<div>
									<blockquote id='impressionView' style={{fontSize: '16px'}}>
									</blockquote>
								</div>
								<div align='right' style={{paddingTop: '10px', paddingRight: '10px'}}>
									<img name='profile_image' src={this.state.profileImage} className='profile_image'/>&nbsp;
									{this.state.nickname} 
								</div>
							</div>
							<textarea id='modifyTextarea' className='form-control' style={{height:'300px', resize:'none'}}></textarea>
							<div align='right' style={{paddingTop: '10px', color: 'red'}} id='detailNoti'></div> 
						</div>
						<div className="modal-footer">
							<button id='confirmDeleteButton' type="button" className='btn btn-danger' onClick={() => this.delete()}>확인</button>
							<button id='deleteContentsButton' type="button" className='btn btn-default' onClick={() => this.changeConfirm()}>삭제</button>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<button id='detailModifyButton' type="button" className='btn btn-default' onClick={() => this.showModify()}>수정</button>
							<button id='detailSaveButton' type="button" className='btn btn-default' onClick={() => this.save()}>저장</button>
							<button type="button" className='btn btn-default' onClick={() => this.saveToClipboardContentsUrl()}>공유</button>
							<button type="button" className="btn btn-default" data-dismiss="modal">닫기</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default DetailModal;