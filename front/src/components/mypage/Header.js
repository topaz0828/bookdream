import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMainView = props.moveMainView;
	}

	render() {
		return (
			<table width='100%'>
				<tbody>
					<tr>
						<td className="h1" style={{paddingTop: '15px', paddingLeft: '10px'}}>
							Bookdream
						</td>
						<td align='right' style={{paddingTop: '15px', paddingRight: '20px'}} width='70px'>
							<button type='button' className='btn btn-info' onClick={this.moveMainView}>
								<span className='glyphicon glyphicon-menu-left' aria-hidden='true'></span>
							</button>
						</td>
					</tr>
				</tbody>
			</table>
		);
	}
}

export default Header;