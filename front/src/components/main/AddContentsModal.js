import React from 'react';

class AddContentsModal extends React.Component {
	componentDidMount() {
		this.searchBookInput = $('#searchBookInput');
		this.searchBookInput.on('keypress', (events) => {
			if (events.keyCode == 13) {
				this.findBook();
			}
		});
		$('#searchBookButton').on('click', () => {
			this.findBook();
		});
		this.impressionBack = $('#impressionBack');
		this.reviewBack = $('#reviewBack');

		this.impressionButton = $('#impressionButton');
		this.impressionButton.on('click', () => {
			this.impressionButton.addClass('active');
			this.impressionBack.show();
			this.reviewButton.removeClass('active');
			this.reviewBack.hide();
		});
		this.reviewButton = $('#reviewButton');
		this.reviewButton.on('click', () => {
			this.impressionButton.removeClass('active');
			this.impressionBack.hide();
			this.reviewButton.addClass('active');
			this.reviewBack.show();
		});
		this.searchResultDropdown = $('#searchResultDropdown');
		// $('#addInputClearButton').on('click', () => {
		// 	$('input[name=impressionInput]').val('');
		// 	$('#reviewInput').val('');
		// });
	}

	findBook() {
		var inputValue = this.searchBookInput.val();
		if (inputValue.length > 0) {
			this.searchResultDropdown.addClass('open');
			// $.ajax({
			// 	type: 'GET',
			// 	url: 'https://dapi.kakao.com/v2/search/book',
			// 	crossDomain: true,
			// 	dataType: 'json',
			// 	beforeSend: function(xhr) {
			// 		xhr.setRequestHeader('Authorization', 'KakaoAK 7b15cef8dfbdedf8cd3fb26ae3262097');
			// 	},
			// 	data: {query: inputValue},
			// 	success: function(res) { 
			// 		console.log(res);
			// 	}
			// });
		}
	}

	renderInputView() {
		return (
			<div>
				<div className="input-group">
					<input id='searchBookInput' type="text" className="form-control" placeholder="Book Finder" aria-describedby="sizing-addon2"/>
					<span className="input-group-btn">
						<button id='searchBookButton' className="btn btn-default" type="button">
							<span className='glyphicon glyphicon-search' aria-hidden='true'></span>
						</button>
					</span>
				</div>
				<div id='searchResultDropdown' className="dropdown" width='100%'>
					<ul className="dropdown-menu" style={{width:'100%'}}>
						<li role="presentation"><a role="menuitem" tabIndex="-1" href="#" style={{fontSize:'17px'}}>Action</a></li>
						<li role="presentation"><a role="menuitem" tabIndex="-1" href="#" style={{fontSize:'17px'}}>Another action</a></li>
						<li role="presentation"><a role="menuitem" tabIndex="-1" href="#" style={{fontSize:'17px'}}>Something else here</a></li>
						<li role="presentation"><a role="menuitem" tabIndex="-1" href="#" style={{fontSize:'17px'}}>Separated link</a></li>
					</ul>
				</div>
			</div>
		);
	}

	renderImpressionAndReviewView() {
		return (
			<div>
				<div style={{paddingTop: '30px', paddingBottom: '20px'}}>
					<ul className="nav nav-tabs">
						<li role="presentation" className="active" id='impressionButton'><a href="#">Impression</a></li>
						<li role="presentation" id='reviewButton'><a href="#">Review</a></li>
					</ul>
				</div>
				<div id='impressionBack' className='form-group'>
					<div style={{paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 1st impression' aria-describedby="sizing-addon2"/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 2nd impression' aria-describedby="sizing-addon2"/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 3rd impression' aria-describedby="sizing-addon2"/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 4st impression' aria-describedby="sizing-addon2"/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 5st impression' aria-describedby="sizing-addon2"/>
					</div>
				</div>
				<div id='reviewBack' hidden='true' className='form-group'>
					<textarea id='reviewInput' className='form-control' style={{height:'260px', resize:'none'}}></textarea>
				</div>
			</div>
		);
	}

	render() {
		return (
			<div className="modal fade" id="addContentsModal" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div className="modal-dialog">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 className="modal-title" id="myModalLabel">Add impression or review</h4>
						</div>
						<div className="modal-body">
							{this.renderInputView()}
							{this.renderImpressionAndReviewView()}
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
							<button type="button" className="btn btn-primary" data-dismiss="modal">Save</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default AddContentsModal;