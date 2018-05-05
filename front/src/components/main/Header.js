import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom'

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMyPage = props.contents.moveMyPage;
		this.refreshBody = props.contents.refreshBody;
		this.contents = props.contents;
	}

	componentDidMount() {
		$('#searchContentsButton').on('click', () => {
			this.getList();
		});
		$('#searchContentsInput').keypress((event) => {
			if (event.which == 13) {
				this.getList();
			}
		});
	}

	getList() {
		var query = $('#searchContentsInput').val().trim();
		this.contents.getList(query);
	}

	openAddModal() {
		$('#addContentsModal').modal({backdrop: 'static'});
	}

	render() {
		return (
			<div className='row' style={{maxWidth: '1300px'}}>
				<div className='col-sm-6 col-md-3' align='center'>
					<span onClick={this.refreshBody} style={{cursor: 'default'}}>
						<h1>Marker</h1><h6><strong>Mark</strong> the moments of your life.</h6>
					</span>
				</div>
				<div className='col-sm-12 col-md-6' style={{paddingTop: '40px'}} align='center'>
					<div className="input-group" style={{maxWidth: '450px'}}>
						<input id='searchContentsInput' type="text" className="form-control" aria-describedby="sizing-addon2"/>
						<span className="input-group-btn">
							<button id='searchContentsButton' className="btn btn-default" type="button">
								<span className='glyphicon glyphicon-search' aria-hidden='true'></span>
							</button>
						</span>
					</div>
				</div>
				<div className='col-sm-6 col-md-3' style={{paddingTop: '35px', paddingRight: '70px'}} align='right'>
					<form method='POST' action='/api/user/logout'>
						<button type='submit' className='btn btn-default'>
							<span className='glyphicon glyphicon-log-out' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<button type='button' className='btn btn-default' onClick={this.refreshBody}>
							<span className='glyphicon glyphicon-refresh' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;
						<button type='button' className='btn btn-default' onClick={this.openAddModal}>
							<span className='glyphicon glyphicon-plus' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;
						<button type='button' className='btn btn-default' onClick={this.moveMyPage}>
							<span className='glyphicon glyphicon-user' aria-hidden='true'></span>
						</button>
						
					</form>
				</div>
			</div>
		);
	}
}

export default Header;