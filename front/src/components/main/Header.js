import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMyPage = props.moveMyPage;
	}

	render() {
		return (
			<table width='100%'>
				<tbody>
					<tr>
						<td className="h1" style={{paddingTop: '15px', paddingLeft: '10px'}}>
							Bookdream
						</td>
						<td align='right' style={{paddingTop: '15px'}}>
							<button type='button' className='btn btn-info' data-toggle='modal' data-target='#addContentsModal'>
								<span className='glyphicon glyphicon-plus' aria-hidden='true'></span>
							</button>
						</td>
						<td align='left' style={{paddingTop: '15px', paddingLeft: '10px'}} width='70px'>
							<button type='button' className='btn btn-info' onClick={this.moveMyPage}>
								<span className='glyphicon glyphicon-user' aria-hidden='true'></span>
							</button>
						</td>
					</tr>
				</tbody>
			</table>
		);
	}
}

export default Header;